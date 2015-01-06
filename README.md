# Binlog

A gem that allows you to parse mysql binlog row events so you can manipulate the stream of 
changes. We are using this to build a real time reporting database.

## Installation

Add this line to your application's Gemfile:

```ruby
gem 'binlog-server'
```

And then execute:

    $ bundle

Or install it yourself as:

    $ gem install binlog-server
    
## Building

    $ mvn package
    $ VERSION="1.2.0" rake bump_version # To change the version in both ruby and java
    $ rake build

## Usage

```ruby
require 'rubygems'
require 'bundler/setup'

class MyPositionCheckpointer
  include Binlog::PositionCheckpointer
  
  # implement the interface here
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
```

## Contributing

1. Fork it ( https://github.com/greenbits/binlog-server/fork )
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create a new Pull Request
