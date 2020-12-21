(ns inmemory-db.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def memory-db (atom {}))
(defn read-db [] @memory-db)
(defn write-db [new-db] (reset! memory-db new-db))

(defn create-table [table-name]
  (let [db (read-db)
        new-db (assoc db table-name {:data [], :indexes {}})]
    (write-db new-db)))

(defn drop-table [table-name]
  (let [db (read-db)
        new-db (dissoc db table-name)]
    (write-db new-db)))

(defn select-* [table-name]
  (get-in (read-db) [table-name :data]))

(defn select-*-where [table-name field field-value]
  (let [db (read-db)
        index (get-in db [table-name :indexes field field-value])
        data (get-in db [table-name :data])]
    (get data index)))

(defn insert [table-name record id-key]
  (if-let [existing-record (select-*-where table-name id-key (id-key record))]
    (println "The record already exists, aborting")
    (let [db (read-db)
        new-db (update-in db [table-name :data] conj record)
        index (dec (count (get-in new-db [table-name :data])))]
    (write-db 
     (update-in new-db [table-name :indexes id-key] assoc (id-key record) index)))))