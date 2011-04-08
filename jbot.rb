require 'rubygems'
require 'isaac'
require 'yammer4r'

last_scrupdate = DateTime.now
yammer = Yammer::Client.new(:config => 'oauth.yml')
swearjar = Hash.new

configure do |c|
  c.nick            = 'jbot'
  c.password        = ''
  c.server          = 'bugcamp.net'
  c.port            = 6667
  c.verbose         = true
end

on :connect do
  join '#bug-dev', '#buglabs'
end

on :channel, /we should/ do
  swearjar[nick] = swearjar[nick].nil? ? 1 : swearjar[nick] + 1
  msg channel, "#{nick} has $#{swearjar[nick]} in the swear jar"
end

on :channel, /jbot[,:] swearjar/ do
  msg channel, "#{nick} has $#{swearjar[nick]} in the swear jar"
end

on :channel, /jbot[,:] scrupdate/ do

  this_scrupdate  = DateTime.now
  found_scrupdate = false

  scrupdates = yammer.messages.collect{ |m| 
    m if (DateTime.parse(m.created_at) > last_scrupdate) and (!( m.body.plain =~ /#scrum/ ).nil?)
  }.compact

  scrupdates.each do |s|
    yammer.users.each do |u|
      if u.id==s.sender_id
        msg channel, "#{u.name}: #{s.body.plain}"
        found_scrupdate = true
      end
    end
  end

  msg channel, "no scrupdates between #{last_scrupdate} and #{this_scrupdate}" unless found_scrupdate

  last_scrupdate = this_scrupdate

end
