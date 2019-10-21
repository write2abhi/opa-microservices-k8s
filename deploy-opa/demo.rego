package httpapi.authz

default allow = false

# Allow users with admin role to access /manage endpoint .
allow {
  input.method == "POST" 
  input.path = ["web-api", "v1", "manage"]    //web-api/v1/manage
  token.payload.role == "admin"
}

# Helper to get the token payload.
token = {"payload": payload} {
  [header, payload, signature] := io.jwt.decode(input.token)
}