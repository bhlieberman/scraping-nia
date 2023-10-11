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

(comment (format-links {:type :footnote
                        :canto-number 1
                        :par-number 4
                        :fn-number 1}))

(doto driver
  (e/go "https://andrewhugill.com/nia/preface.html")
  (e/exists? [{:tag :a :fn/link "nia1thesis.html"}]))

(e/click driver [{:tag :a :fn/link "nia1thesis.html"}])

(e/get-element-inner-html driver [{:tag :body}])

(defn navigate! [root]
  (let [page (str root )]))

(comment
  ;; in case of exception
  (pprint *e))