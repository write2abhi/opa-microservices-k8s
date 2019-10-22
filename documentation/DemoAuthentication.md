## Demo: Authentication

In order to authenticate users, you need an OpenID identity provider. 

You can use [IBM App ID](https://console.bluemix.net/catalog/services/appid) or you can use an OpenID identity provider of your choice.

Before running the scripts below, make sure you can [access the IBM Cloud](SetupIBMCloudAccess.md).


**Create new App ID service instance**

Run the following command to create these artifacts:

* App ID service instance 'app-id-cloud-native'
* App ID Cloud Foundry alias 'app-id-cloud-native'
* App ID credentials
* App ID test user 'user@demo.email, verysecret'
* App ID test admin 'admin@demo.email, verysecret'
* App ID redirect URL

```
$ ibm-scripts/create-app-id.sh
```
**Reuse an existing App ID service instance**

The IBM Cloud lite plan only allows one App ID instance in your organization. If you have an App ID instance, you can use it rather than creating a new one. 

In this case paste the App ID credentials in [authentication-nodejs/.env](../authentication-nodejs/.env). Check out [authentication-nodejs/.env.template](../authentication-nodejs/.env.template) for an example. Additionally paste APPID_ISSUER and APPID_JWKS_URI in [local.env](local.env). See [template.local.env](template.local.env) for an example.


**Use another OpenID identify provider**

You can use any OpenID identity provider. In this case paste the endpoint URLs in [authentication-nodejs/.env](../authentication-nodejs/.env). Check out [authentication-nodejs/.env.template](../authentication-nodejs/.env.template) for an example. Additionally paste APPID_ISSUER and APPID_JWKS_URI in [local.env](local.env). See [template.local.env](template.local.env) for an example.

**Adding custom attributes to user profile**

You can add custom attributes to user profiles and get these attributes in JWT token to write authorization policies based on the attributes. In this demo we will setup custom attributes based on user roles. Let's first grant the necessary IAM permission to access APP ID management APIs. 

- Login to IBM cloud with credential you created in previous step. [URL to Login](https://cloud.ibm.com/login)
- Go to Manage > Acess (IAM) > Service Ids.
- Click on service id name and switch to Access policies tab. Now access the role created here. 
- Now select Administrator in Platform acces field and Reader, Writer and Manager in Service access field. Save the changes.

Now we will add the custom attributes to users already created by create-app-id.sh script. 

- Go to Resource List > Services and you will see one service instance is created. click on app-id instance name. 
- Go to User Profiles and you will see two user already created. Now click on admin user id listed under **Identifier** field and edit the Custom Attributes field. You can add any arbitary JSON objects here. Add below attribute and Save. 
```
{
  "role": "admin"
}
```
- Similarly, create below attribute for demo user. 
```
{
  "role": "developer"
}
```
Now we can map these attributes to access and identity token claims to store the custom attributes in the token themselves. 

- Log in to IBM Cloud by using the CLI.
```
ibmcloud login
```
- Obtain an IAM access token.
```
ibmcloud iam oauth-tokens
```
- Make a request to the token configuration endpoint.
```
curl --request PUT \
https://us-south.appid.cloud.ibm.com/management/v4/{{APPID_TENANT_ID}}/config/tokens \
--header 'Authorization: Bearer <iam-access-token>' \
--header 'Content-Type: application/json' \
-d '{
   "access": {
       "expires_in": 3601
   },
   "refresh": {
       "enabled": false,
       "expires_in": 2592001
   },
   "anonymousAccess": {
       "expires_in": 2592001
   },
   "idTokenClaims": [
   {
   "source": "attributes",
   "sourceClaim": "role"
   }
   ]
}'
```
A tenant ID is how your instance of App ID is identified in the request. You can find your ID in the Service credentials tab of the dashboard. If you don't have a set, you can follow the steps in the GUI to create credentials.


**Set up the Demo**

Invoke the following commands to set up the demo. 

```
$ cd $PROJECT_HOME
$ scripts/check-prerequisites.sh
$ scripts/delete-all.sh
$ scripts/deploy-articles-java-jee.sh
$ scripts/deploy-web-api-java-jee.sh
$ scripts/deploy-authors-nodejs.sh
$ scripts/deploy-authentication-nodejs.sh
$ scripts/deploy-web-app-vuejs-authentication.sh
$ scripts/deploy-istio-ingress-v1.sh
$ scripts/show-urls.sh
```
After running the scripts above, you will get a list of all URLs in the terminal.

<kbd><img src="../images/urls.png" /></kbd>

Example URL to open the web app: http://192.168.99.100:31380

Example API endpoint: http://192.168.99.100:31380/web-api/v1/getmultiple

Open the web application with the URL that is displayed as output of 'scripts/show-urls.sh'. When you click 'Login', use the credentials of the demo user.

To access the JWT token stored after use login you can install the Vue.js devtools Chrome extention [here](https://chrome.google.com/webstore/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd). 

After the login, the Vue.js application stores the id_token if the Vuex state. By default id_token value is base64 encoded, so you can decode it online [here](https://jwt.io/). You should see "role" field in decoded JWT token. 

<kbd><img src="../images/login.jpeg" /></kbd>

Check out the [animated gif](../images/login.gif) to see the authentication flow.

<kbd><img src="../images/login.gif" /></kbd>

References:
https://cloud.ibm.com/docs/services/appid?topic=appid-tutorial-roles
https://github.com/ibm-cloud-security/appid-postman
