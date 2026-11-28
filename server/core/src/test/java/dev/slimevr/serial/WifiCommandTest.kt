package dev.slimevr.serial

import kotlin.test.Test
import kotlin.test.assertEquals

class WifiCommandTest {
	@Test
	fun `encodes Wi-Fi credentials as UTF-8 base64`() {
		assertEquals(
			"SET BWIFI RXhhbXBsZVNTSUQrUGx1cw== RXhhbXBsZVBhc3NAMTIz\n",
			buildSetWifiCommand("ExampleSSID+Plus", "ExamplePass@123"),
		)
	}

	@Test
	fun `sends explicit empty password argument`() {
		assertEquals(
			"SET BWIFI T3Blbk5ldA== \"\"\n",
			buildSetWifiCommand("OpenNet", null),
		)
	}
}
