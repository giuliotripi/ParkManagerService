/* Generated by AN DISI Unibo */
package it.unibo.ctxbasicrobot
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts(
	        "basicrobot.local", this, "parkingsystem.pl", "sysRules.pl"
	)
}

