package com.buglabs.bug.ircbot.behavior.easyshucks

import com.buglabs.bug.ircbot.behavior.easylistening.EasyListener

class EasyShucks < EasyListener
  def registration()
    onChannel "^attaboy" do |e|
      return "thanks, i think."
    end
    returns void
  end
end
