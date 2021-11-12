# Dynami-UI
Dynami-UI is the user interface application to execute Dynami-Runtime


**Work in progress!!!**

Main class: 
<code>
org.dynami.ui.DynamiApplication 
</code>

### Fix missing library on Maven repository

<code>
mvn install:install-file -Dfile=./libs/extfx/extfx/0.3/extfx-0.3.jar  -DgroupId=extfx  -DartifactId=extfx -Dversion=0.3 -Dpackaging=jar -DgeneratePom=true
</code