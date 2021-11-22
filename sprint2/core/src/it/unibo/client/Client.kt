/* Generated by AN DISI Unibo */ 
package it.unibo.client

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Client ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("clientGUI init")
						delay(5000) 
					}
				}	 
				state("printslotnum") { //this:State
					action { //it:State
						println("[CLIENT] receiving")
						if( checkMsgContent( Term.createTerm("slotNum(SLOTNUM)"), Term.createTerm("slotNum(SLOTNUM)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("[CLIENT] Received ${payloadArg(0)}")
						}
						println("[CLIENT] received")
						request("carEnter", "carEnter(_)" ,"parkingservice" )  
					}
					 transition(edgeName="t229",targetState="printtoken",cond=whenReply("token"))
				}	 
				state("printtoken") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("token(TOKENID)"), Term.createTerm("token(TOKENID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("[CLIENT] Received ${payloadArg(0)}")
						}
					}
					 transition( edgeName="goto",targetState="trip", cond=doswitch() )
				}	 
				state("trip") { //this:State
					action { //it:State
						println("[CLIENT] Going out for a trip")
						delay(15000) 
						println("[CLIENT] Trying to pick up my car")
						request("pickUpRequest", "pickUpRequest(t0)" ,"parkingservice" )  
					}
					 transition(edgeName="t330",targetState="pickUp",cond=whenReply("pickUpReply"))
				}	 
				state("pickUp") { //this:State
					action { //it:State
						println("[CLIENT] CAR PICKED UP")
					}
				}	 
			}
		}
}
