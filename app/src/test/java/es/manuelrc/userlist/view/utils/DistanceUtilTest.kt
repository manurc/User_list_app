package es.manuelrc.userlist.view.utils

import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkAll
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class DistanceUtilTest: TestCase() {

    private lateinit var distanceUtil: DistanceUtil

    @Before
    override fun setUp() {
        distanceUtil = spyk(recordPrivateCalls = true)
        every { distanceUtil.invoke("deg2rad") withArguments listOf(any<Double>())} answers { callOriginal()}
        every { distanceUtil.invoke("rad2deg") withArguments listOf(any<Double>())} answers { callOriginal()}

    }

    @After
    fun onAfter() {
        unmockkAll()
    }

    @Test
    fun testDistanceInKmZeroValues() {
        val result = distanceUtil.distanceInKm(0.0,0.0,0.0,0.0)
        assert(result == 0.0)
    }

    @Test
    fun testDistanceInKmSuccess() {
        val twoPointDistance = 624.6917088653319
        val latitude1 = 40.4662939
        val longitude1 = 3.6914398
        val latitude2 = 40.4618616
        val longitude2 = -3.6952593
        val result = distanceUtil.distanceInKm(latitude1,longitude1,latitude2,longitude2)
        assert(result == twoPointDistance)
    }
}