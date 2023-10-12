(ns scraping.nia
  (:require [babashka.pods :as pods]
            [etaoin.api :as e]
            [taoensso.timbre :as timbre]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [clojure.walk :refer [postwalk postwalk-demo]]
            [scraping.utils :refer [format-links]]))

;; set logging to info per docs
(timbre/set-level! :info)

;; instantiate driver
(def driver (e/firefox))

;; load bootleg pod
(pods/load-pod 'retrogradeorbit/bootleg "0.1.9")

(require '[pod.retrogradeorbit.bootleg.html :refer [html]])

(def cantos #{1 2 4})

(def page-root "https://andrewhugill.com/nia/preface.html")

(defn follow-link? [l]
  (let [hash (str/split l #"\#")]
    (str/ends-with? hash "ret")))

;; describe page structure in data
;; TBD if useful
(def ^{:depth 0
       :root page-root}
  page-structure [[{:links [{:par
                             (into []
                                   (for [i (range 5)]
                                     {:level i
                                      :content nil
                                      :footnotes []}))}]
                    :content nil}]])

(defn navigate! [root link-data]
  (e/with-firefox-headless driver
    (let [_ (e/go driver root)
          url (format-links link-data)]
      (if (e/exists? driver [{:tag :a :fn/link url}])
        (e/click driver [{:tag :a :fn/link url}])
        (throw (ex-info "no such hyperlink on page" {})))
      (let [body (e/get-element-inner-html driver [{:tag :body}])
            links? (e/exists? driver [{:tag :a}])
            link-data (atom [])]
        (if links?
          ;; DON'T FOLLOW LINKS IF THEY CONTAIN RETURN TEXT!
          (doseq [link (e/query-all driver [{:tag :a}])]
            (e/click driver link)
            (swap! link-data conj (e/get-element-inner-html driver [{:tag :body}])))
          (spit (str/replace url "html" "edn") (html body :data)))))))

(comment
  ;; working minimally!!
  (navigate! page-root {:type :thesis
                        :number 1})
  ;; in case of exception
  (pprint *e))