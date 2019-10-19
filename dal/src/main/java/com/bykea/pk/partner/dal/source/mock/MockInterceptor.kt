package com.bykea.pk.partner.dal.source.mock

import com.bykea.pk.partner.dal.BuildConfig
import okhttp3.*

/**
 * This will help us to test our networking code while a particular API is not implemented
 * yet on Backend side.
 */
class MockInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (BuildConfig.DEBUG) {
            val uri = chain.request().url().uri().toString()
            val responseString = when {
                uri.endsWith("f_distance=5&sort=nearby") -> getListOfJobsJson
                else -> ""
            }

            return chain.proceed(chain.request())
                    .newBuilder()
//                    .code(SUCCESS_CODE)
                    .protocol(Protocol.HTTP_1_1)
                    .message(responseString)
                    .body(ResponseBody.create(MediaType.parse("application/json"),
                            responseString.toByteArray()))
                    .addHeader("content-type", "application/json")
                    .build()
        } else {
            //just to be on safe side.
            throw IllegalAccessError("MockInterceptor is only meant for Testing Purposes and " +
                    "bound to be used only with DEBUG mode")
        }
    }

}

const val getListOfJobsJson = """
{
  "code": 200,
  "data": [
    {
      "dt": "2019-10-17T10:07:43.358Z",
      "service_code": 25,
      "pickup": {
        "address": "F 1, Jinnah Housing Society, Karachi",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8667,
        "lng": 67.0809
      },
      "id": 16514,
      "dropoff": {
        "zone_en": "?",
        "zone_ur": "?"
      }
    },
    {
      "dt": "2019-10-17T10:08:06.283Z",
      "service_code": 25,
      "pickup": {
        "address": "F 1, Jinnah Housing Society, Karachi",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8667,
        "lng": 67.0809
      },
      "id": 16515,
      "dropoff": {
        "zone_en": "?",
        "zone_ur": "?"
      }
    },
    {
      "dt": "2019-10-17T08:30:18.579Z",
      "service_code": 27,
      "pickup": {
        "address": "Shahrah-e-Faisal Road, Jinnah Housing Society, Karachi",
        "zone_en": "Nazimabad",
        "zone_ur": "شمالی ناظم آباد ٹاؤن",
        "lat": 24.8666,
        "lng": 67.0808
      },
      "id": 16500,
      "dropoff": {
        "address": "F 1, Jinnah Housing Society, Karachi",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8667,
        "lng": 67.0809
      }
    },
    {
      "dt": "2019-10-17T09:44:41.752Z",
      "dropoff": {
        "address": "F 1, Jinnah Housing Society, Karachi",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8667,
        "lng": 67.0809
      },
      "pickup": {
        "address": "Mirch Masala",
        "zone_en": "Bahadarabad",
        "zone_ur": "شمالی ناظم آباد ٹاؤن",
        "lat": 24.8659,
        "lng": 67.0809
      },
      "service_code": 28,
      "id": 16509,
      "fare_est": "33"
    },
    {
      "dt": "2019-10-17T10:08:47.480Z",
      "dropoff": {
        "address": "F 1, Jinnah Housing Society, Karachi",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8667,
        "lng": 67.0809
      },
      "pickup": {
        "address": "Mirch Masala",
        "zone_en": "Bahadarabad",
        "zone_ur": "شمالی ناظم آباد ٹاؤن",
        "lat": 24.8659,
        "lng": 67.0809
      },
      "service_code": 29,
      "id": 16517,
      "fare_est": "33"
    },
    {
      "dt": "2019-10-17T10:37:24.856Z",
      "dropoff": {
        "address": "F 1, Jinnah Housing Society, Karachi",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8667,
        "lng": 67.0808
      },
      "pickup": {
        "address": "Mirch Masala",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8659,
        "lng": 67.0809
      },
      "service_code": 30,
      "id": 16525,
      "fare_est": "33"
    },
    {
      "dt": "2019-10-17T10:49:34.189Z",
      "dropoff": {
        "address": "F 1, Jinnah Housing Society, Karachi",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8667,
        "lng": 67.0809
      },
      "pickup": {
        "address": "Mirch Masala",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8659,
        "lng": 67.0809
      },
      "service_code": 21,
      "id": 16526,
      "fare_est": "33"
    },
    {
      "dt": "2019-10-17T14:53:17.592Z",
      "dropoff": {
        "address": "F 1, Jinnah Housing Society, Karachi",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8667,
        "lng": 67.0809
      },
      "pickup": {
        "address": "Mirch Masala",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8659,
        "lng": 67.0809
      },
      "service_code": 22,
      "id": 16553,
      "fare_est": "33"
    },
    {
      "dt": "2019-10-17T10:08:23.290Z",
      "dropoff": {
        "address": "F 1, Jinnah Housing Society, Karachi",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8667,
        "lng": 67.0809
      },
      "pickup": {
        "address": "Mirch Masala",
        "zone_en": "Bahadarabad",
        "zone_ur": "بہادرآباد",
        "lat": 24.8659,
        "lng": 67.0809
      },
      "service_code": 23,
      "id": 16516,
      "fare_est": "33"
    },
    {
            "dt": "2019-08-02T06:20:35.336Z",
            "dropoff": {
                "address": "ouou",
                "zone_en": "Cantt",
                "zone_ur": "کینٹ",
                "lat": 24.8534,
                "lng": 67.056
            },
            "pickup": {
                "address": "oye",
                "zone_en": "Bahadarabad",
                "zone_ur": "بہادرآباد",
                "lat": 24.8667,
                "lng": 67.0808
            },
            "service_code": 21,
            "id": 76,
            "fare_est": "84"
        },
        {
            "dt": "2019-10-07T13:32:19.364Z",
            "dropoff": {
                "address": "Food Family",
                "zone_en": "PECHS",
                "zone_ur": "پی-ای-سی-ایچ-ایس",
                "lat": 24.8666,
                "lng": 67.0747
            },
            "pickup": {
                "address": "F 1, Jinnah Housing Society, Karachi",
                "zone_en": "Bahadarabad",
                "zone_ur": "بہادرآباد",
                "lat": 24.8667,
                "lng": 67.0809
            },
            "service_code": 25,
            "id": 124,
            "fare_est": "38"
        },
        {
            "dt": "2019-10-18T11:39:08.594Z",
            "pickup": {
                "address": "Bangalore Town Block A Bangalore Town, Karachi, Karachi City, Sindh",
                "zone_en": "Bahadarabad",
                "zone_ur": "بہادرآباد",
                "lat": 24.8753,
                "lng": 67.0705
            },
            "service_code": 30,
            "id": 42,
            "fare_est": 70,
            "dropoff": {
                "zone_en": "?",
                "zone_ur": "?"
            }
        },
        {
            "dt": "2019-10-18T11:39:09.545Z",
            "pickup": {
                "address": "Bangalore Town Block A Bangalore Town, Karachi, Karachi City, Sindh",
                "zone_en": "Bahadarabad",
                "zone_ur": "بہادرآباد",
                "lat": 24.8753,
                "lng": 67.0705
            },
            "service_code": 30,
            "id": 43,
            "fare_est": 70,
            "dropoff": {
                "zone_en": "?",
                "zone_ur": "?"
            }
        },
        {
            "dt": "2019-10-18T11:51:18.828Z",
            "pickup": {
                "address": "Bangalore Town Block A Bangalore Town, Karachi, Karachi City, Sindh",
                "zone_en": "Bahadarabad",
                "zone_ur": "بہادرآباد",
                "lat": 24.8753,
                "lng": 67.0705
            },
            "service_code": 28,
            "id": 46,
            "fare_est": 70,
            "dropoff": {
                "zone_en": "?",
                "zone_ur": "?"
            }
        },
        {
            "dt": "2019-10-18T11:50:57.554Z",
            "pickup": {
                "address": "Bangalore Town Block A Bangalore Town, Karachi, Karachi City, Sindh",
                "zone_en": "Bahadarabad",
                "zone_ur": "بہادرآباد",
                "lat": 24.8753,
                "lng": 67.0705
            },
            "service_code": 27,
            "id": 45,
            "fare_est": 70,
            "dropoff": {
                "zone_en": "?",
                "zone_ur": "?"
            }
        },
        {
            "dt": "2019-10-18T11:51:27.871Z",
            "pickup": {
                "address": "Bangalore Town Block A Bangalore Town, Karachi, Karachi City, Sindh",
                "zone_en": "Bahadarabad",
                "zone_ur": "بہادرآباد",
                "lat": 24.8753,
                "lng": 67.0705
            },
            "service_code": 29,
            "id": 47,
            "fare_est": 70,
            "dropoff": {
                "zone_en": "?",
                "zone_ur": "?"
            }
        }
  ]
}
"""
