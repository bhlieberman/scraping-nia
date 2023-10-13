(ns scraping.nia
  (:require [babashka.pods :as pods]
            [babashka.curl :as curl]
            [etaoin.api :as e]
            [taoensso.timbre :as timbre]
            [clojure.pprint :refer [pprint]]
            [scraping.utils :refer [format-links improve-hiccup]]))

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

(defn process-resp [resp]
  (-> resp :body (html :data) improve-hiccup))

(defn navigate-to-urls! []
  (map-indexed
   (fn [i [thesis [& parens]]]
     (let [resp-body (try (-> (str page-root thesis) curl/get process-resp)
                          (catch Exception _
                            (println "could not find " thesis)))]
       (swap! page-text update-in [:canto i :body] conj resp-body))
     (doseq [p parens]
       (let [resp-body (try (-> (str page-root p) curl/get process-resp)
                            (catch Exception _
                              (println "could not find " parens)))]
         (swap! page-text update-in [:canto i :parens] conj resp-body)))) urls))

(comment
  ;; IT WORKS!
  (pprint (get-in @page-text [:canto 0 :parens 3]))
  ;; better almost working code!
  (navigate-to-urls!)
  )