#!/bin/bash

C_INGESTR_HOME="${HOME}/.ingestr"

if [[ ! -d "${C_INGESTR_HOME}" ]]; then
  echo "The directory ${C_INGESTR_HOME} does not exist and will be created"
  mkdir  "${C_INGESTR_HOME}"
fi

echo "Performing local install..."
JAR_FILE=$(ls target/ingestr-cli*.jar | head)
rm "${C_INGESTR_HOME}/ingestr.jar"

echo "Copying ${JAR_FILE} to ${C_INGESTR_HOME}/ingesr.jar..."

cp "${JAR_FILE}" "${C_INGESTR_HOME}/ingestr.jar"
cp "src/bin/ingestr" "${C_INGESTR_HOME}/ingestr"

chmod +x "${C_INGESTR_HOME}/ingestr"

