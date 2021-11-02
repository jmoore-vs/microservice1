def microservicio = 'microservice-1'
def namespace = 'juan-maldonado02-dev'
def repo_git = 'https://github.com/jmoore-vs/microservice1.git'
def rama = 'main'
def openshift_server_endpoint = 'https://api.sandbox-m2.ll9k.p1.openshiftapps.com:6443'
def openshift_token = 'sha256~HrkZXhOUzb7IFxT4Yrcp5lJ9WWPnDU0ANEwGsYHO5Aw'

def nombre_servicio2 = 'microservice-2'
def nombre_servicio_ddbb = 'db4free'
	
pipeline {
    agent any
    environment {
        mavenHome = tool 'maven'
	PATH = "$mavenHome/bin:$PATH"
    }
    stages {
    	stage('Pull') {
    	    steps {
                script {
                	openshift.withCluster() {
				openshift.withProject() {
				    echo "Pull repo a node local"

				    //clean local repo
				    sh "rm -rf /var/lib/jenkins/workspace/${microservicio}/*"

			            //pull project
				    git branch: "${rama}", url: "${repo_git}"

			            sh """
					echo $PATH
					mvn --version
					oc version
					oc login --token=${openshift_token} --server=${openshift_server_endpoint}
					oc project ${namespace}

				    """
				}
		       }
                }
            }
    	}
    	stage('Compile') {
    	    steps {
                script {
                	openshift.withCluster() {
			    openshift.withProject() {
				echo "Compile code"
				sh """
				      pwd
				      ls -l
				      echo "mvn clean install"
				"""
			    }
			}
                }
            }
    	}
	stage('Cleanup') {
    	     steps {
                script {
                	openshift.withCluster() {
			    openshift.withProject() {
				echo "Cleanup de openshift resources"
		                sh """
				     oc get all --selector app=${microservicio} > resp
				     [ -s resp ] && oc delete all --selector app=${microservicio}-build && oc delete all --selector app=${microservicio}

				"""
		            }
			}
                }
            }
    	}
    	stage('Create') {
    	     steps {
                script {
                	openshift.withCluster() {
			    openshift.withProject() {
				echo "Create openshift resources"
				    
				sh """			
				    oc process microservice-build-template \
				      -p MICROSERVICE=${microservicio} \
				      -p NAMESPACE=${namespace} \
				      -p SOURCE_REPOSITORY_URL=${repo_git} \
				      -p SOURCE_REPOSITORY_REF=${rama} \
				      | oc create -f -

				"""
			    }
			}
                }
            }
    	}
	stage('build') {
	     steps {
		   script {
			   openshift.withCluster() {
			       openshift.withProject( '' ) {
				     echo "Start build de openshift resources"

				     sh """	    
					oc start-build ${microservicio}-build --follow

				     """
						
			       }
		           }
		   }
	     }
	}
	stage('deploy') {
	     steps {
		   script {
			   String service2_temp = nombre_servicio2.toUpperCase().replaceAll("-","_")
			   String ddbb_temp = nombre_servicio_ddbb.toUpperCase().replaceAll("-","_")
			   
			   openshift.withCluster() {
			       openshift.withProject() {
				      echo "Deploy de openshift resources"

				      sh """
				          
					  oc new-app ${microservicio}-build --name ${microservicio} \
					      -e ${service2_temp}_SERVICE_HOST=${nombre_servicio2} \
					      -e ${ddbb_temp}_SERVICE_HOST=${nombre_servicio_ddbb}

					  #en caso de exponer el microservice externamente
					  oc expose svc/${microservicio}

				      """


				}
			    }
		    }
	      }
	}
    	
    }
}

