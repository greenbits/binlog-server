# coding: utf-8
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'binlog/version'

Gem::Specification.new do |spec|
  spec.name          = "binlog-server"
  spec.version       = Binlog::VERSION
  spec.authors       = ["Ben Curren"]
  spec.email         = ["ben@greenbits.com"]
  spec.summary       = %q{MySQL row based replication binlog server based on open replication.}
  spec.description   = %q{MySQL row based replication binlog server based on open replication.}
  spec.homepage      = "http://github.com/greenbits/binlog-server"
  spec.license       = "MIT"
  spec.platform      = 'java'

  spec.files         = `git ls-files -z *.rb`.split("\x0") +
                       Dir['target/binlog-server*.jar'] +
                       `git ls-files -z *.gemspec`.split("\x0") +
                       `git ls-files -z Rakefile`.split("\x0") +
                       `git ls-files -z *.md`.split("\x0")
  spec.executables   = spec.files.grep(%r{^bin/}) { |f| File.basename(f) }
  spec.test_files    = spec.files.grep(%r{^(test|spec|features)/})
  spec.require_paths = ["target", "lib"]

  spec.add_development_dependency "bundler", "~> 1.6"
  spec.add_development_dependency "rake", "~> 10.0"
end
