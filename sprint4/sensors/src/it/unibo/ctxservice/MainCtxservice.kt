/* Generated by AN DISI Unibo */
package it.unibo.ctxservice
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts(
	        "service.local", this, "parkingsystem.pl", "sysRules.pl"
	)
}

