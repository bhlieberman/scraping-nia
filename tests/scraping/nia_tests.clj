(ns scraping.nia-tests
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [scraping.utils :refer [format-links canto-uris]]
            [scraping.nia :refer [process-resp]]))

(deftest format-links-correctly-creates-thesis
  (testing "that `format-links` multimethod correctly creates thesis links"
    (let [thesis {:type :thesis
                  :number 1}]
      (is (= "nia1thesis.html" (format-links thesis))))))

(deftest format-links-correctly-creates-parentheses
  (testing "that `format-links` multimethod correctly creates parenthesis links"
    (let [parenthesis {:type :parenthesis
                       :canto-number 1
                       :par-number 2}]
      (is (= "nia1par2.html" (format-links parenthesis))))))

(deftest format-links-correctly-creates-footnotes
  (testing "that `format-links` multimethod correctly creates footnote links"
    (let [footnote {:type :footnote
                    :canto-number 4
                    :par-number 4
                    :fn-number 1}]
      (is (= "nia4par.html#fn1" (format-links footnote))))))

(deftest resp-to-hiccup
  (testing "that a dummy http response is formatted into hiccup"
    (is (= [:div "Hello world"] (process-resp {:body "<body><div>Hello world</div></body>"})))))

(run-tests)