(ns scraping.nia
  (:require [babashka.pods :as pods]
            [etaoin.api :as e]
            [taoensso.timbre :as timbre] 
            [clojure.pprint :refer [pprint]]
            [scraping.utils :refer [format-links]]))

;; set logging to info per docs
(timbre/set-level! :info)

;; instantiate driver
(def driver (e/firefox))

;; load bootleg pod
(pods/load-pod 'retrogradeorbit/bootleg "0.1.9")

(require '[pod.retrogradeorbit.bootleg.html :refer [html]])

(def cantos [1 2 4])

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

(def page-text (atom {:canto (into []
                                   (repeat 3 {:body []
                                              :parens []}))}))

(def page-root "https://andrewhugill.com/nia/")

(defn navigate-to-urls! []
  (e/with-firefox driver
    (map-indexed
     (fn [i [thesis [& parens]]]
       (e/go driver (str page-root thesis))
       (e/wait 3)
       (let [el-text (e/get-element-inner-html driver {:tag :body})]
         (swap! page-text update-in [:canto i :body] conj el-text))
       (doseq [p parens]
         (e/go driver (str page-root p))
         (let [el-text (e/get-element-inner-html driver {:tag :body})]
           (swap! page-text update-in [:canto i :parens] conj el-text))
         (e/wait 3))) urls)))

(comment
  (e/get-user-agent driver)
  urls
  ;; better almost working code!
  (navigate-to-urls!)
  ;; in case of exception
  (pprint *e))