require 'rubygems'
require 'binlog-server'

class MyPositionCheckpointer
  attr_reader :file_name, :position
  include Binlog::PositionCheckpointer
  
  def initialize(file_name, position)
    rotate(file_name, position)
  end
  
  def rotate(file_name, position)
    @file_name = file_name
    @position = position
    puts "rotate: #{@file_name}, #{@position}"
  end
  
  def checkpoint(position)
    @position = position
    puts "checkpoint: #{@position}"
  end
end

class MyRowEventListener
  include Binlog::RowEventListener

  def startup(version)
    puts "Startup"
  end

  def shutdown
    puts "Shutdown"
  end

  def begin_transaction
    puts "Begin transaction"
  end

  def on_update(event)
    puts "Update"
  end

  def on_write(event)
    puts "Write"
  end

  def on_delete(event)
    puts "Delete"
  end
  
  def on_error(error)
    puts "Error"
  end

  def commit_transaction
    puts "Commit Transaction"
  end
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
