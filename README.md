## Open Policy Agent integration with Microservices

This project consists sample code to deploy cloud-native-starter microservice on k8s with OPA to enforce authorization policies. 

The microservices can easily be deployed on Kubernetes environments running [Istio](https://istio.io/) like [Minikube](https://kubernetes.io/docs/setup/minikube/), [IBM Cloud Kubernetes Service](https://www.ibm.com/cloud/container-service), [Minishift](https://docs.okd.io/latest/minishift/index.html) or [OpenShift on the IBM Cloud](https://cloud.ibm.com/docs/containers?topic=containers-openshift_tutorial).

The project showcases the following functionality:

* [Containerized Java EE Microservices](documentation/DemoJavaImage.md)
* [Exposing REST APIs](documentation/DemoExposeRESTAPIs.md)
* [Consuming REST APIs](documentation/DemoConsumeRESTAPIs.md)
* [Traffic Routing](documentation/DemoTrafficRouting.md)
* [Resiliency](documentation/DemoResiliency.md)
* [Authentication and Authorization](documentation/DemoAuthentication.md)
* [Metrics](documentation/DemoMetrics.md)
* [Health Checks](documentation/DemoHealthCheck.md)
* [Configuration](documentation/DemoConfiguration.md)
* [Distributed Logging and Monitoring](documentation/DemoDistributedLoggingMonitoring.md)
* [Persistence with Java Persistence API (JPA)](documentation/DemoJPA.md)

This diagram shows the key components:

<kbd><img src="images/architecture-2.png" /></kbd>

The next screenshot shows the web application. More screenshots are in the [images](images) folder.

<kbd><img src="images/web-app.png" /></kbd>


### Setup

The sample application can be run in four different environments:

1) **Minikube** (locally): See instructions below
2) **IBM Cloud Kubernetes Service** - see [instructions](documentation/IKSDeployment.md)
3) **Minishift** (locally) - see [instructions](documentation/MinishiftDeployment.md)
4) **OpenShift on the IBM Cloud** - see [instructions](documentation/OpenShiftIKSDeployment.md)

The following instructions describe how to install everything locally on **Minikube**.

**Important:** Before the microservices can be installed, make sure you've set up Minikube and Istio correctly or follow these [instructions](documentation/SetupLocalEnvironment.md) to set up Minikube and Istio from scratch. This should not take longer than 30 minutes.

Before deploying the application, get the code:

```
$ git clone https://github.com/IBM/cloud-native-starter.git
$ cd cloud-native-starter
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

**Deploy Microservices:**

```
$ cd ${ROOT_FOLDER}

bash scripts/deploy-articles-java-jee.sh \ && scripts/deploy-web-api-java-jee.sh \ && scripts/deploy-authors-nodejs.sh \ && scripts/deploy-authentication-nodejs.sh \ && scripts/deploy-web-app-vuejs-authentication.sh \ && scripts/deploy-istio-ingress-v1.sh \ && scripts/show-urls.sh
```

After running the scripts above, you will get a list of all URLs in the terminal.

<kbd><img src="images/urls.png" /></kbd>

Example URL to open the web app: http://192.168.99.100:31380

Example API endpoint: http://192.168.99.100:31380/web-api/v1/getmultiple

At this point you have seen the "base line" of our Cloud Native Starter. The following documents describe how to implement additional functionality:

* [Containerized Java EE Microservices](documentation/DemoJavaImage.md)
* [Exposing REST APIs](documentation/DemoExposeRESTAPIs.md)
* [Consuming REST APIs](documentation/DemoConsumeRESTAPIs.md)
* [Traffic Routing](documentation/DemoTrafficRouting.md)
* [Resiliency](documentation/DemoResiliency.md)
* [Authentication and Authorization](documentation/DemoAuthentication.md)
* [Metrics](documentation/DemoMetrics.md)
* [Health Checks](documentation/DemoHealthCheck.md)
* [Configuration](documentation/DemoConfiguration.md)
* [Distributed Logging and Monitoring](documentation/DemoDistributedLoggingMonitoring.md)
* [Persistence with Java Persistence API (JPA)](documentation/DemoJPA.md)

We will mostly focus on Authentication and Authorization part. Authentication part will be managed by IBM AppID and Authorization part will be managed by Open Policy Agent(OPA). 

**Deploy OPA:**
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

### Cleanup

Run the following command to delete all cloud-native-starter components from Istio.

```
$ scripts/delete-all.sh
```

You can also delete single components:

```
$ scripts/delete-articles-java-jee.sh
$ scripts/delete-articles-java-jee-quarkus.sh
$ scripts/delete-web-api-java-jee.sh
$ scripts/delete-authors-nodejs.sh
$ scripts/delete-web-app-vuejs.sh
$ scripts/delete-istio-ingress.sh
```

