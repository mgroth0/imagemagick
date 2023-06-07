package matt.imagemagick

import matt.lang.anno.SeeURL
import matt.lang.optArray
import matt.model.data.file.FilePath
import matt.shell.ControlledShellProgram
import matt.shell.Shell

val <R> Shell<R>.convert get() = ConvertCommand(this)


data class ImageMagickOptions(
    val density: String? = null,
    val colorize: Int? = null,
    val background: String? = null,
    val crop: String? = null,
    val resize: String? = null,
    val define: String? = null,
    val colors: Int? = null,
) {
    fun inputOptions() = arrayOf(
        *optArray(density) { arrayOf("-density", this) },
        *optArray(colorize) { arrayOf("-colorize", this.toString()) },
        *optArray(background) { arrayOf("-background", this) },
    )

    fun outputOptions() = arrayOf(
        *optArray(crop) { arrayOf("-crop", this) },
        *optArray(resize) { arrayOf("-resize", this) },
        *optArray(define) { arrayOf("-define", this) },
        *optArray(colors) { arrayOf("-colors", this.toString()) },
    )
}

class ConvertCommand<R>(shell: Shell<R>) : ImageMagickCommand<R>(
    shell = shell,
    program = "/opt/homebrew/bin/convert",
) {
    fun run(
        inputs: List<FilePath>,
        options: ImageMagickOptions? = null,
        output: FilePath
    ) = sendCommand(
        *optArray(options) { inputOptions() },
        * inputs.map { it.filePath }.toTypedArray(),
        *optArray(options) { outputOptions() },
        output.filePath
    )
}

@SeeURL("https://codeyarns.com/tech/2014-11-15-how-to-crop-image-using-imagemagick.html#gsc.tab=0")
@SeeURL("https://imagemagick.org/script/command-line-processing.php#geometry")
@SeeURL("https://imagemagick.org/script/command-line-options.php#crop")
@SeeURL("https://linux.die.net/man/1/mogrify")
@SeeURL("https://stackoverflow.com/questions/9992174/imagemagick-command-to-convert-and-save-with-same-name")
val <R> Shell<R>.mogrify get() = MogrifyCommand(this)

class MogrifyCommand<R>(shell: Shell<R>) : ImageMagickCommand<R>(
    shell = shell,
    program = "/opt/homebrew/bin/mogrify",
) {
    fun run(
        file: FilePath,
        options: ImageMagickOptions? = null,
    ) = sendCommand(
        *optArray(options) { inputOptions() },
        *optArray(options) { outputOptions() },
        file.filePath
    )
}


sealed class ImageMagickCommand<R>(shell: Shell<R>, program: String) : ControlledShellProgram<R>(
    shell = shell,
    program = program,
) {
    fun listFormats() = sendCommand("-list", "format")
}
