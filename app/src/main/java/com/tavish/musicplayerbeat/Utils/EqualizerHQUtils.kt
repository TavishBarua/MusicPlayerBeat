
import com.h6ah4i.android.media.opensl.audiofx.OpenSLHQEqualizer
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerContext
import com.h6ah4i.android.media.audiofx.IEqualizer


object EqualizerHQUtils {
    val NUMBER_OF_BANDS: Int
    val NUMBER_OF_PRESETS: Int
    val PRESETS: Array<PresetInfo>?
    val CENTER_FREQUENCY: IntArray
    val BAND_LEVEL_MIN: Short
    val BAND_LEVEL_MAX: Short

    class PresetInfo {
        var index: Short = 0
        var name: String? = null
        var settings: IEqualizer.Settings? = null
    }

    init {
        var numberOfBands: Short = 0
        var numberOfPresets: Short = 0
        var presets: Array<PresetInfo>? = null
        var centerFreqency = IntArray(0)
        var bandLevelRange = ShortArray(2)

        var oslmp_context: OpenSLMediaPlayerContext? = null
        var eq: IEqualizer? = null
        try {
            val params = OpenSLMediaPlayerContext.Parameters()
            params.options = OpenSLMediaPlayerContext.OPTION_USE_HQ_EQUALIZER

            oslmp_context = OpenSLMediaPlayerContext(null, params)
            eq = OpenSLHQEqualizer(oslmp_context)

            numberOfBands = eq.numberOfBands
            numberOfPresets = eq.numberOfPresets

            presets = Array(numberOfPresets.toInt()){ PresetInfo() }
            // Apparently a bad piece of shit written below
            for (i in 0 until numberOfPresets) {
                val preset = PresetInfo()

                eq.usePreset(i.toShort())

                preset.index = i.toShort()
                preset.name = eq.getPresetName(preset.index)
                preset.settings = eq.properties

                presets[i] = preset
            }

            centerFreqency = IntArray(numberOfBands.toInt())
            for (i in 0 until numberOfBands) {
                centerFreqency[i] = eq.getCenterFreq(i.toShort())
            }

            bandLevelRange = eq.bandLevelRange
        } catch (e: UnsupportedOperationException) {
            // just ignore (maybe API level is less than 14)
        } finally {
            try {
                eq?.release()
            } catch (e: Exception) {
            }

            try {
                oslmp_context?.release()
            } catch (e: Exception) {
            }

            eq = null
            oslmp_context = null
        }

        NUMBER_OF_BANDS = numberOfBands.toInt()
        NUMBER_OF_PRESETS = numberOfPresets.toInt()
        PRESETS = presets
        CENTER_FREQUENCY = centerFreqency
        BAND_LEVEL_MIN = bandLevelRange[0]
        BAND_LEVEL_MAX = bandLevelRange[1]
    }
}
