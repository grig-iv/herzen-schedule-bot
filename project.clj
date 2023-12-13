(defproject schedule-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [enlive "1.1.6"]
                 [clojure.java-time "1.4.2"]
                 [morse "0.4.3"]]
  :main schedule-bot.core
  :aot [schedule-bot.core]
  :target-path "target/%s"
  :plugins [[cider/cider-nrepl "0.42.1"]]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})

