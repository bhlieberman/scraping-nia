(ns scraping.nia
  (:require [etaoin.api :as e]
            [taoensso.timbre :as timbre]
            [clojure.pprint :refer [pprint]]
            [scraping.utils :refer [format-links]]))

;; set logging to info per docs
(timbre/set-level! :info)

;; instantiate driver
(def driver (e/firefox))

(def cantos #{1 2 4})

(def page-root "https://andrewhugill.com/nia/preface.html")

(e/get-element-inner-html driver [{:tag :body}])

(defn navigate! [root link-data]
  (e/with-firefox-headless driver
    (let [_ (e/go driver root)
          url (format-links link-data)]
      (if (e/exists? driver [{:tag :a :fn/link url}])
        (e/click driver [{:tag :a :fn/link url}])
        (throw (ex-info "no such hyperlink on page" {})))
      (let [body (e/get-element-inner-html driver [{:tag :body}])]
        (pprint body)))))

(comment
  ;; working minimally!!
  (navigate! page-root {:type :thesis
                        :number 1})
  ;; in case of exception
  (pprint *e))