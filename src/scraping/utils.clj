(ns scraping.utils)

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