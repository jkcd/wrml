#                                                                             
#  WRML - Web Resource Modeling Language                                      
#   __     __   ______   __    __   __                                        
#  /\ \  _ \ \ /\  == \ /\ "-./  \ /\ \                                       
#  \ \ \/ ".\ \\ \  __< \ \ \-./\ \\ \ \____                                  
#   \ \__/".~\_\\ \_\ \_\\ \_\ \ \_\\ \_____\                                 
#    \/_/   \/_/ \/_/ /_/ \/_/  \/_/ \/_____/                                 
#                                                                             
# http://www.wrml.org                                                         
#                                                                             
# Copyright 2011 - 2013 Mark Masse (OSS project WRML.org)                     
#                                                                             
# Licensed under the Apache License, Version 2.0 (the "License");             
# you may not use this file except in compliance with the License.            
# You may obtain a copy of the License at                                     
#                                                                             
# http://www.apache.org/licenses/LICENSE-2.0                                  
#                                                                             
# Unless required by applicable law or agreed to in writing, software         
# distributed under the License is distributed on an "AS IS" BASIS,           
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
# See the License for the specific language governing permissions and         
# limitations under the License.                                              
#             

# CoffeeScript


do (Marionette) ->
  _.extend Marionette.Renderer,

    lookups: ["apps/", "components/"]

    render: (template, data) ->

# TODO: Is this needed to support embedded templates?
#      if (template.startsWith("#"))
#        Marionette.Renderer.render(template, data)
#        return

      ecoTemplate = @getEcoTemplate(template)
      throw "Template #{template} not found!" unless ecoTemplate
      ecoTemplate(data)

    getEcoTemplate: (template) ->
      for path in [template, template.split("/").insertAt(-1, "templates").join("/")]
        for lookup in @lookups
          ecoTemplate = ecoTemplates[lookup + path]
          return ecoTemplate if ecoTemplate