# Evidence capture checklist

Capture genuine screenshots after running each step. Suggested filenames:

1. `01-git-branch-merge.png` - `git log --oneline --graph --decorate --all`
2. `02-jenkins-job-config.png` - Jenkins job SCM and script path
3. `03-jenkins-success.png` - Jenkins console, green result and archived artifact
4. `04-jenkins-junit.png` - JUnit results showing the actual test count
5. `05-github-actions.png` - actual successful workflow run
6. `06-ansible-playbook.png` - play recap showing failed=0
7. `07-docker-build-run.png` - Docker build and running container
8. `08-kubernetes-three-pods.png` - three Ready pods and Service
9. `09-api-response.png` - curl response through the Kubernetes Service
10. `10-kubernetes-scale-five.png` - five Ready pods
11. `11-kubernetes-scale-two.png` - two Ready pods
12. `12-kubernetes-probes-configmap.png` - manifest or `kubectl describe` output

Screenshots should include enough terminal/window context to identify the tool and command. Never alter terminal text or manufacture a success state.
