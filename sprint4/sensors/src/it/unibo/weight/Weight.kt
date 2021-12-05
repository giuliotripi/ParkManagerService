/* Generated by AN DISI Unibo */ 
package it.unibo.weight

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Weight ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 var WEIGHT = 0; var simulate: Boolean = false;  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						solve("consult('config.pl')","") //set resVar	
						solve("simulate(X)","") //set resVar	
						println(currentSolution)
						 val x = getCurSol("X").toString() 
								   simulate = ( x == "on")
						println("Weight init simulate=$simulate")
					}
					 transition( edgeName="goto",targetState="idle", cond=doswitch() )
				}	 
				state("idle") { //this:State
					action { //it:State
					}
					 transition(edgeName="t08",targetState="handleRequest",cond=whenRequest("getValue"))
					transition(edgeName="t09",targetState="handleSetWeight",cond=whenDispatch("setValue"))
				}	 
				state("handleRequest") { //this:State
					action { //it:State
						
									if(!simulate) {
										val p  = Runtime.getRuntime().exec("python3 weight.py")
										val reader = java.io.BufferedReader(java.io.InputStreamReader(p.getInputStream()))
										WEIGHT = reader.readLine().toInt()
									}
						answer("getValue", "sensorValue", "sensorValue($WEIGHT,weight)"   )  
					}
					 transition( edgeName="goto",targetState="idle", cond=doswitch() )
				}	 
				state("handleSetWeight") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("sensorValue(VALUE,SENSORNAME)"), Term.createTerm("sensorValue(WEIGHT,weight)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												if(simulate) {
													WEIGHT = payloadArg(0).toInt()
												}
						}
					}
					 transition( edgeName="goto",targetState="idle", cond=doswitch() )
				}	 
			}
		}
}
