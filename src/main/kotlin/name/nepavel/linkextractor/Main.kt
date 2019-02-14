package name.nepavel.linkextractor

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("USAGE: java -jar LinkExtractor.jar site.to.extract/links")
        return
    }
    val iterator = ExtractorCore(args[0]).iterator()
    while(iterator.hasNext()) {
        val next = iterator.next()
        val url = next.first
        val content = next.second //тут лежит содержимое страницы (если оно тебе нужно для парсинга)
    }
}
