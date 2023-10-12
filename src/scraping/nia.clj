(ns scraping.nia
  (:require [babashka.pods :as pods]
            [etaoin.api :as e]
            [taoensso.timbre :as timbre]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [scraping.utils :refer [format-links]]))

;; set logging to info per docs
(timbre/set-level! :info)

;; instantiate driver
(def driver (e/firefox))

;; load bootleg pod
(pods/load-pod 'retrogradeorbit/bootleg "0.1.9")

(require '[pod.retrogradeorbit.bootleg.html :refer [html]])

(def cantos #{1 2 4})

(def urls (into [] (comp
                    cat
                    (partition-all 2))
                (for [canto cantos
                      :let [thesis (format-links {:type :thesis
                                                  :number canto})
                            parentheses (for [i (range 1 6)]
                                          (format-links {:type :parenthesis
                                                         :canto-number canto
                                                         :par-number i}))]]
                  [thesis parentheses])))

(def page-text (atom {:canto {1 {:body []
                                 :parens []}
                              2 {:body []
                                 :parens []}
                              4 {:body []
                                 :parens []}}}))

(def page-root "https://andrewhugill.com/nia/")

(defn navigate-to-urls! []
  (e/with-firefox driver
    (doseq [[thesis [& parens]] urls
            :let [p (doseq [p parens]
                      (e/go driver (str page-root p))
                      (let [el-text (e/get-element-inner-html driver {:tag :body})]
                        (swap! page-text update-in [:canto 1 :parens] conj el-text))
                      (e/wait 1))]]
      parens)))

(comment
  ;; better almost working code!
  (navigate-to-urls!)
  ;; in case of exception
  (pprint *e))