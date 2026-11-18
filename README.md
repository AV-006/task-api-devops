# Task API - DevOps Assignment

**Student:** Arjun Vinod  
**USN:** 1MS24CI022  
**Course:** DevOps (25CIAEC59 / 25CYAEC59)

A small Spring Boot REST API used to demonstrate one continuous delivery path:

**Git -> Maven -> Jenkins CI -> Ansible -> Docker -> Kubernetes -> Scale**

> This repository is a ready-to-run implementation scaffold. Run the commands in your own environment and capture genuine screenshots before submission. No fake Jenkins, Docker, Ansible, or Kubernetes execution screenshots are included.

## 1. Application

The API uses an in-memory list (no database) and exposes:

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/` | Application, version, environment and instance information |
| GET | `/tasks` | List tasks |
| GET | `/tasks/{id}` | Fetch one task; 404 if absent |
| POST | `/tasks` | Add a task; requires a non-blank title |
| GET | `/health` | Basic health response |
| GET | `/actuator/health` | Spring Boot health status |

Default port: `8080`. Environment variables: `APP_PORT`, `APP_ENV`, `APP_VERSION`.

## 2. Prerequisites

- Git
- JDK 17
- Maven 3.9+
- Jenkins with Pipeline, Git, JUnit and Maven Integration plugins; configure tools named `jdk17` and `maven3`
- Docker Desktop / Docker Engine
- Ansible on Ubuntu / WSL2 (with systemd available on the managed host)
- Minikube and kubectl

For a Windows laptop, use PowerShell for Git, Maven, Docker and Minikube; use Ubuntu in WSL2 for Ansible. Enable systemd in WSL2 if you use WSL itself as the managed Linux host, or use an Ubuntu VM.

## 3. Build and run locally

PowerShell:

```powershell
mvn -B clean test
mvn -B package -DskipTests
$env:APP_ENV = 'local'
java -jar target/task-api.jar
```

In another terminal:

```powershell
curl.exe http://localhost:8080/
curl.exe http://localhost:8080/tasks
curl.exe http://localhost:8080/actuator/health
curl.exe -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -d '{"title":"Prepare DevOps demo"}'
```

A successful Maven test run ends with `BUILD SUCCESS`. Do not copy sample output into the report as observed evidence; capture your actual console.

## 4. Git branching and merge evidence

The local repository is initialized with two feature branches and visible `--no-ff` merge commits as a starting demonstration. Inspect it with:

```bash
git log --oneline --graph --decorate --all
git branch -a
```

Before pushing, set your own Git identity if needed:

```bash
git config user.name "Arjun Vinod"
git config user.email "YOUR_GITHUB_NOREPLY_OR_EMAIL"
```

Create a new empty GitHub repository, then connect and push:

```bash
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/task-api-devops.git
git push -u origin main
git push origin feature/ci-pipeline feature/deployment
```

Replace the placeholders in `repo_link.txt` and the report after the repository is created. Add the course coordinator as a collaborator as required by the assignment.

## 5. Jenkins CI

1. Start Jenkins and install Pipeline, Git and JUnit plugins.
2. In **Manage Jenkins -> Tools**, configure a JDK installation named `jdk17` (Java 17) and Maven installation named `maven3` (Maven 3.9+).
3. Create a Pipeline job. Select **Pipeline script from SCM**, Git, your repository URL, branch `*/main`, and script path `Jenkinsfile`.
4. Save and run once manually. The first run registers `pollSCM`; later commits are detected by the one-minute poll.
5. Confirm the Test and Build stages are green, JUnit results are published, and a versioned JAR is archived.
6. Push a small change and capture Jenkins starting a new run because of an SCM change.

The CI enhancement is the Test stage before packaging. The archive name contains both the Jenkins build number and short Git commit SHA, making the artifact traceable.

## 6. GitHub Actions

The workflow at `.github/workflows/ci.yml` runs tests and packaging on each push and pull request to `main`. Push the repository to GitHub, then open **Actions** and capture the successful run.

## 7. Ansible configuration management

Build the JAR first. Run the playbook from the `ansible` directory on Ubuntu / WSL2 / an Ubuntu VM:

```bash
cd ansible
ansible --version
ansible-playbook -i inventory/hosts.ini site.yml --ask-become-pass
```

The inventory targets the local Linux host with a local connection. The `task_api` role installs the Java runtime, creates a dedicated service user, copies the JAR, installs a systemd unit, and starts the service. The `app_port` variable is passed from the playbook. The `Restart Task API` handler runs only when the JAR or service unit changes. The final URI task checks `/actuator/health`.

This playbook assumes an Ubuntu/Debian managed host with systemd and sudo privileges. It is not intended to run directly in plain Windows PowerShell.

## 8. Docker

From the repository root:

```bash
docker build -t task-api:1.0.0 .
docker run --rm -p 8080:8080 -e APP_ENV=docker task-api:1.0.0
```

The Dockerfile has a Maven/JDK build stage and a smaller JRE runtime stage. The runtime process runs as a non-root `app` user.

## 9. Kubernetes on Minikube

Start Docker Desktop first. From the repository root:

```bash
bash scripts/deploy-minikube.sh
kubectl get pods -l app=task-api
kubectl get deployment task-api
kubectl get svc task-api-service
minikube service task-api-service --url
```

Use the URL printed by `minikube service` to test `/`, `/tasks` and `/actuator/health`. The service is a NodePort Service on port `30080` inside Minikube. The Deployment starts with three replicas; the labels and selectors use `app: task-api`.

### Scaling demonstration

```bash
bash scripts/scale-demo.sh
# or run each step separately:
kubectl scale deployment/task-api --replicas=5
kubectl get pods -l app=task-api
kubectl scale deployment/task-api --replicas=2
kubectl get pods -l app=task-api
kubectl scale deployment/task-api --replicas=3
```

### Deployment enhancement

The ConfigMap provides `APP_PORT`, `APP_ENV` and `APP_VERSION` independently of the image. Readiness and liveness probes call Spring Boot Actuator endpoints. Readiness controls whether a pod receives Service traffic; liveness detects a stuck process and allows Kubernetes to restart it.

## 10. Evidence checklist

Capture the following from your own execution. The report contains matching clearly marked slots:

- Git graph showing `main`, both feature branches and merge commits.
- Jenkins job SCM configuration, successful console, JUnit test report and archived JAR.
- GitHub Actions successful workflow run.
- Ansible playbook output ending with `failed=0`; run a second time to show idempotence if time permits.
- Docker image build and running container/API response.
- Kubernetes `get pods`, `get svc` and actual API response.
- Pod listing at 3, 5, 2 (then back to 3) replicas.
- ConfigMap and readiness/liveness probe configuration or describe output.

Use `evidence/README.md` for suggested filenames. Do not present diagrams as proof of successful tool execution.

## 11. Repository structure

```text
src/                 Spring Boot source and JUnit tests
Jenkinsfile           Jenkins pipeline
.github/workflows/    GitHub Actions CI
ansible/              Inventory, playbook and task_api role
Dockerfile            Multi-stage container build
k8s/                  ConfigMap, Deployment and Service
scripts/              Local, Docker and Kubernetes helper scripts
report/                Report assets and source diagrams
```

## Troubleshooting notes

- `mvn` not found: install Maven 3.9+ and reopen the terminal; confirm `mvn -version`.
- Port 8080 already in use: stop the other process or set `APP_PORT=8081` and update the host port mapping.
- Ansible cannot connect or restart systemd: run inside an Ubuntu VM/WSL2 with systemd enabled; use `sudo systemctl status task-api`.
- Kubernetes pods show `ImagePullBackOff`: confirm the image tag is `task-api:1.0.0` and run `minikube image load task-api:1.0.0`.
- Pods are Running but not Ready: inspect `kubectl describe pod <pod>` and `kubectl logs <pod>`; readiness remains false until the Actuator endpoint responds.

### Start Jenkins locally with Docker Compose

The optional Compose file starts Jenkins with persistent storage:

```powershell
docker compose -f ci/docker-compose.yml up -d
docker exec task-api-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Open `http://localhost:9090`, complete the first-time setup, install the Pipeline, Git, JUnit and Maven Integration plugins, then configure the JDK and Maven tools described above. The Jenkins controller image includes Java; configure a Java 17 tool named `jdk17` and Maven 3.9+ named `maven3` to match the Jenkinsfile.

Automatic Jenkins CI trigger verification.
