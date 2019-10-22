## Open Policy Agent integration with Microservices

This project consists sample code to deploy cloud-native-starter microservice on k8s with OPA to enforce authorization policies. 

The microservices can easily be deployed on Kubernetes environments running [Istio](https://istio.io/) like [Minikube](https://kubernetes.io/docs/setup/minikube/), [IBM Cloud Kubernetes Service](https://www.ibm.com/cloud/container-service), [Minishift](https://docs.okd.io/latest/minishift/index.html) or [OpenShift on the IBM Cloud](https://cloud.ibm.com/docs/containers?topic=containers-openshift_tutorial).

The project showcases the following functionality:

* [Authentication and Authorization](documentation/DemoAuthentication.md)

This diagram shows the key components:

<kbd><img src="images/architecture-2.png" /></kbd>

### Setup

The sample application can be run in four different environments:

1) **Minikube** (locally): See instructions below
2) **IBM Cloud Kubernetes Service** - see [instructions](documentation/IKSDeployment.md)
3) **Minishift** (locally) - see [instructions](documentation/MinishiftDeployment.md)
4) **OpenShift on the IBM Cloud** - see [instructions](documentation/OpenShiftIKSDeployment.md)

The following instructions describe how to install everything locally on **Minikube**.

**Important:** Before the microservices can be installed, make sure you've set up Minikube and Istio correctly or follow these [instructions](documentation/SetupLocalEnvironment.md) to set up Minikube and Istio from scratch. This should not take longer than 30 minutes.

**Note:** This demo is tested with Minikube and Istio default setup from original repo [here](https://github.com/IBM/cloud-native-starter). However Istio is not a requirement.

Before deploying the application, get the code:

```
$ git clone https://github.com/write2abhi/opa-microservices-k8s.git
$ cd opa-microservices-k8s
$ ROOT_FOLDER=$(pwd)
```

The microservices can be installed via scripts. In addition to Minikube and Istio you need the following tools to be available.

Prerequisites:

* [docker](https://docs.docker.com/install/)
* [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
* [curl](https://curl.haxx.se/download.html)
* [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)

Docker always needs to be installed locally. The tools git, curl and kubectl (and ibmcloud) can be installed locally or you can use a [Docker image](https://cloud.ibm.com/docs/cli?topic=cloud-cli-using-idt-from-docker) that comes with these tools.

```
$ cd ${ROOT_FOLDER}
$ docker run -v $ROOT_FOLDER/:/cloud-native-starter -it --rm ibmcom/ibm-cloud-developer-tools-amd64
```
## Setup Authentication with IBM App ID
Before deploying the microservices on Minikube you need to create an account on IBM Cloud and configure App ID instance. Refer the documentation below.  

[Setup authentication with IBM APP ID](documentation/DemoAuthentication.md)

At this stage we have successfully configured role based user authentication. Next, we will deploy and configure OPA to enforce authorization policies. 

## Authorization via OPA
In order to protect functionality on a more fine-grained level, authorization can be handled with Open Policy Agent(OPA) rather than writing it in the business logic of microservices. 

Deploy the OPA server in same K8s cluster where microservices are deployed. By default it will be deployed in opa namespace. 
```
kubectl apply -f deploy-opa-k8s/k8s-menifests/
```
Get the URL of OPA using minikube:
```
OPA_URL=$(minikube service opa --url -n opa)
```
Now you can query OPA’s API:
```
curl $OPA_URL/v1/data
```
OPA will respond with the greeting from the policy (the pod hostname will differ):
```
{
  "result": {
    "example": {
      "greeting": "hello from pod \"opa-78ccdfddd-xplxr\"!"
    }
  }
}
```
At this point we have verified that OPA is up and running. 

**Load OPA policy:**

To inject the OPA policy run the following command:
```
curl -X PUT --data-binary @deploy-opa-k8s/demo.rego \
  $OPA_URL/v1/policies/demo
```

To test the authorization flow, login to the web application and access 'Manage Application' from dropdown which triggers the endpoint 'manage' of the 'web-api' microservice.

Only the user 'admin@demo.email' with 'admin' role is allowed to invoke this endpoint. 

<kbd><img src="images/authorization-microprofile-admin.png" /></kbd>

For the user 'user@demo.email' an error is thrown.

<kbd><img src="images/authorization-microprofile-user.png" /></kbd>

Watch the [animated gif](images/authorization-microprofile.gif) to see the flow in action.

You can refere the source code of /manage API endpoint [here](web-api-java-jee/src/main/java/com/ibm/webapi/apis/Manage.java). By default this microservice call OPA server with service name as http://opa.opa:8181. you can configure it according to your OPA service url. 

### Cleanup

Run the following command to delete all cloud-native-starter components from Istio.

```
$ scripts/delete-all.sh
```

You can also delete single components:

```
$ scripts/delete-articles-java-jee.sh
$ scripts/delete-web-api-java-jee.sh
$ scripts/delete-authors-nodejs.sh
$ scripts/delete-authentication-nodejs.sh
$ scripts/delete-web-app-vuejs-authentication.sh
$ scripts/delete-istio-ingress-v1.sh
```

