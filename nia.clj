(ns nia
  (:require [etaoin.api :as e]
            [taoensso.timbre :as timbre]
            [clojure.pprint :refer [pprint]]))

;; set logging to info per docs
(timbre/set-level! :info)

;; instantiate driver
(def driver (e/firefox))

(def cantos #{1 2 4})

(defn canto-uris [n] (str "nia" n "%s.html"))

(defmulti format-links (fn [{:keys [type]}] type))
(defmethod format-links :thesis [{:keys [number]}]
  (format (canto-uris number) "thesis"))
(defmethod format-links :parenthesis [{:keys [canto-number par-number]}]
  (format (canto-uris canto-number) (str "par" par-number)))
(defmethod format-links :footnote [{:keys [canto-number par-number fn-number]}]
  (str (format (canto-uris canto-number)
               (or (str "par" par-number) "")) "#fn"  
       fn-number))

(comment (format-links {:type :footnote
                        :canto-number 1
                        :par-number 4
                        :fn-number 1}))

(doto driver
  (e/go "https://andrewhugill.com/nia/preface.html")
  (e/exists? [{:tag :a :fn/link "nia1thesis.html"}]))

(e/click driver [{:tag :a :fn/link "nia1thesis.html"}])

(e/get-element-text driver {:tag :a :fn/link "nia1thesis.html"})

(comment 
  ;; in case of exception
  (pprint *e))