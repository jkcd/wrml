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

@Wrmldoc.module "ModelApp", (ModelApp, App, Backbone, Marionette, $, _) ->
  @startWithParent = false

  class ModelApp.Router extends Marionette.AppRouter
    #	appRoutes:
    # ":uri/edit" : "edit"
    #		":uri" : "show"

  #API =
  #  show: (wrmlData) ->
  #    new ModelApp.Show.Controller(wrmlData)
  #region: App.mainRegion


  #App.addInitializer ->
  #	new ModelApp.Router
  #		controller: API

  @showView = (wrmlData) ->
    new ModelApp.Show.Controller(wrmlData)

  ModelApp.on "start", (wrmlData) ->
    @showView(wrmlData)


#newModel: (region) ->
#	new ModelApp.New.Controller
#		region: region

#edit: (uri, model) ->
#	new ModelApp.Edit.Controller
#		uri: uri
#		model: model

#App.commands.setHandler "new:model", (region) ->
#	API.newModel region

#App.vent.on "model:clicked model:created", (model) ->
#	App.navigate model.uri
#	API.edit model.uri, model

#App.vent.on "model:cancelled model:updated", (model) ->
#	App.navigate model.uri
#	API.show()
	
