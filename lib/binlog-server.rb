require "binlog/version"
require "binlog-server-#{Binlog::VERSION}.jar"

module Binlog
  include_package "com.greenbits.binlog"
end
