/* Generated by AN DISI Unibo */ 
package it.unibo.sonar

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Sonar ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 var VALUE = 0  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Sonar init")
					}
					 transition( edgeName="goto",targetState="idle", cond=doswitch() )
				}	 
				state("idle") { //this:State
					action { //it:State
					}
					 transition(edgeName="t064",targetState="handleRequest",cond=whenRequest("getValue"))
					transition(edgeName="t065",targetState="handleSetValue",cond=whenDispatch("setValue"))
				}	 
				state("handleRequest") { //this:State
					action { //it:State
						answer("getValue", "sensorValue", "sensorValue($VALUE)"   )  
					}
					 transition( edgeName="goto",targetState="idle", cond=doswitch() )
				}	 
				state("handleSetValue") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("sensorValue(VALUE)"), Term.createTerm("sensorValue(STATE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 VALUE = payloadArg(0).toInt()  
						}
					}
					 transition( edgeName="goto",targetState="idle", cond=doswitch() )
				}	 
			}
		}
}