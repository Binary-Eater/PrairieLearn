(ns autograder-wrapper.core
  (:require [autograder-wrapper.autograder :as ag]
            [clojure.string :as str]
            [clojure.data.json :as json]))

(defrecord grading_result [output succeeded tests score])
(defrecord test_frame [name description points max_points output message])

(defn removeFromEnd
  [s end]
  (cond (.endsWith s end)
        (removeFromEnd(.substring s 0 (- (count s) (count end))) end)
        :else s)
  )

(defn generateTestRecs
  [ln]
  (def test_result (test_frame. (removeFromEnd (ln 0) "\n") (removeFromEnd (ln 1) "\n") (read-string (removeFromEnd (ln 2) "\n")) (read-string (removeFromEnd (ln 3) "\n")) (removeFromEnd (ln 4) "\n") (removeFromEnd (ln 5) "\n")))
  (cond (< (count (subvec ln 6)) 6)
        '(test_result)
        :else (conj (generateTestRecs(subvec ln 6)) test_result))
  )

(defn countEarnedPoints
  [ln]
  (cond (< (count (subvec ln 6)) 6)
        (read-string (removeFromEnd (ln 2) "\n"))
        :else (+ (read-string (removeFromEnd (ln 2) "\n")) (countEarnedPoints (subvec ln 6))))
  )

(defn countTotalPoints
  [ln]
  (cond (< (count (subvec ln 6)) 6)
        (read-string (removeFromEnd (ln 3) "\n"))
        :else (+ (read-string (removeFromEnd (ln 3) "\n")) (countEarnedPoints (subvec ln 6))))
  )

(defn main
  []
  (def output (ag/main))
  (def lines (str/split-lines output))
  (def result (grading_result. output false '() 0.0))

  (cond (and (> (count lines) 1) (= (mod (count lines) 6) 0))
        (do
          (def result (assoc result :succeeded true))
          (def result (assoc result :tests (generateTestRecs lines)))
          (def result (assoc result :score (/ (float (countEarnedPoints lines)) (float (countTotalPoints lines))))))
    :else 0)

  (print (json/write-str result))

  )

(main)
