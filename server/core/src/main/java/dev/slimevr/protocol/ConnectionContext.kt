package dev.slimevr.protocol

class ConnectionContext {
    val dataFeedList: MutableList<DataFeed> = ArrayList()

    val subscribedTopics: MutableList<Int> = ArrayList()

    private var useSerial = false

    private var useProvisioning = false
    private var useAutoBone = false

    fun useSerial(): Boolean {
        return useSerial
    }

    fun setUseSerial(useSerial: Boolean) {
        this.useSerial = useSerial
    }

    fun useAutoBone(): Boolean {
        return useAutoBone
    }

    fun setUseAutoBone(useAutoBone: Boolean) {
        this.useAutoBone = useAutoBone
    }

    fun useProvisioning(): Boolean {
        return useProvisioning
    }

    fun setUseProvisioning(useProvisioning: Boolean) {
        this.useProvisioning = useProvisioning
    }
}
