package name.nepavel.linkextractor

import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

open class ExtractorCore(val url: String, val param: String = "") : Iterable<Pair<String, Optional<Elements>>> {
    private val log = LoggerFactory.getLogger(ExtractorCore::class.java)

    private val toVisit: LinkedBlockingDeque<String> = LinkedBlockingDeque()
    private val visited: MutableSet<String> = HashSet(100)

    override fun iterator(): Iterator<Pair<String, Optional<Elements>>> {
        visited.clear()
        toVisit.clear()
        extractLinks(Jsoup.connect(url + param).get().allElements)
        visited.add(url)
        log.info("Visited: {}", url)
        return object : Iterator<Pair<String, Optional<Elements>>> {
            override fun hasNext(): Boolean = toVisit.isNotEmpty()

            override fun next(): Pair<String, Optional<Elements>> {
                val nextUrl = toVisit.poll() ?: return "" to Optional.empty()
                visited.add(nextUrl)
                log.info("Visited: {}", nextUrl)
                return nextUrl to Optional.ofNullable(
                    Jsoup.connect(nextUrl + param).get().allElements?.also {
                        extractLinks(it)
                    })
            }
        }
    }

    open fun Elements.filterLinkContainers(): Elements = this

    open fun Elements.extractLinksToVisit(): List<String> = flatMap { it.getElementsByTag("a") }
        .map { it.attr("abs:href") }

    private fun extractLinks(elements: Elements) {
        toVisit.addAll(elements
            .filterLinkContainers()
            .extractLinksToVisit()
            .filter {
                !visited.contains(it)
                        && !toVisit.contains(it)
                        && !it.endsWith(".jpg", true)
                        && !it.endsWith(".jpeg", true)
                        && !it.endsWith(".png", true)
            }
            .distinct())
    }
}