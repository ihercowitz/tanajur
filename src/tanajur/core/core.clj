(ns tanajur.core.core
  (:require [clj-http.client :as httpcli]
            [net.cgrand.enlive-html :as html]))


(defn- query-format [search]
  (clojure.string/replace search #" " "%20"))

(defn- kickass-search-url [search]
  (->> (query-format search)
       (str "http://kickass.to/usearch/")))

(defn- get-page [search]
  (-> (:body (httpcli/get (kickass-search-url search)))
      java.io.StringReader.
      html/html-resource))

(defn- get-first-link [page]
  (-> page
      (html/select [:a.imagnet])
      first
      :attrs
      :href))

(defn get-kickass-link [search]
  (-> (get-page search)
      get-first-link))


(defn serie [name]
  (-> (get-kickass-link name)
      println))

(defn- read-command []
  (let [cmd (read-string (str "(" (read-line) ")"))]
    (cons (first cmd)
          (rest cmd))))

(defn console []
  (loop [input (read-command)]
    (when-not (= :quit (keyword (first input)))
      (eval input)
      (recur (console)))))


