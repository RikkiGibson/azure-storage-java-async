set version=2.0.6
set url=https://github.com/Azure/autorest.java/releases/download/v%version%/microsoft.azure-autorest.java-%version%.tgz
autorest %~dp0README.md --use=%url% --version=2.0.4210
