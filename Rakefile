require 'bundler/gem_tasks'

desc "bump the version to ENV['VERSION']"
task :bump_version do
  %x{
    sed -i '' 's/VERSION = ".*"/VERSION = "#{ENV['VERSION']}"/' lib/binlog/version.rb;
    sed -i '' 's/^  <version>.*<\\/version>/  <version>#{ENV['VERSION']}<\\/version>/' pom.xml;
    mvn package
  }
end
