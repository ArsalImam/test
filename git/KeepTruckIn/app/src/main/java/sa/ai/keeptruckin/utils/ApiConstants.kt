package sa.ai.keeptruckin.utils

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * api constants for the app
 */
object ApiConstants {
    object Request {
        const val QUERY = "name_startsWith"
        const val MAX_ROWS = "maxRows"
        const val USERNAME = "username"
        const val DEFAULT_MAX_PER_PAGE = 5
        const val STATUS_SUCCESS = 5
    }

    object ErrorMessage {
        const val SOMETHING_WENT_WRONGE = "Something went wrong!"
    }

    const val SEARCH_CITIES = "searchJSON"
}