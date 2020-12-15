package io.phdata.examples

import java.sql.Timestamp

case class NasaLab(
                    agency: String,
                    lab_name: String,
                    facility: String,
                    center_search_status: Option[String],
                    occupied_year: Option[Int],
                    status: Option[String],
                    url_link: Option[String],
                    record_date: Option[Timestamp],
                    last_update_date: Option[Timestamp],
                    address: Option[String],
                    city: Option[String],
                    state: Option[String],
                    country: Option[String]
                  )
