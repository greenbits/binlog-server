require 'rubygems'
gem 'binlog-server'
require 'binlog'

class MyPositionCheckpointer
  attr_reader :file_name, :position
  include Binlog::PositionCheckpointer
  
  def initialize(file_name, position)
    rotate(file_name, position)
  end
  
  def rotate(file_name, position)
    @file_name = file_name
    @position = position
  end
  
  def checkpoint(position)
    @position = position
  end
end

class MyRowEventListener
  include Binlog::RowEventListener
  
  # implement the interface here
end

server = Binlog::RowServer.new
server.user = 'replicate'
server.password = 'test1234'
server.host = 'localhost'
server.port = 3306
server.serverId = 10
server.checkpointer = MyPositionCheckpointer.new('mysql-bin.000007', 6957)
server.listener = MyRowEventListener.new
server.start
