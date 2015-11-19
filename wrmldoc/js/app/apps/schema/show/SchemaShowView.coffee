#                                                                             
#  WRML - Web Resource Schemaing Language                                      
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

@Wrmldoc.module "SchemaApp.Show", (Show, App, Backbone, Marionette, $, _) ->
  class Show.Schema extends App.Views.ItemView
    template: "schema/show/schema_show"

    events:
      'keyup .wrml-model-property-input' : 'handleModelPropertyInputKeyup'
      'keyup .schema-slot-name-input' : 'handleSchemaSlotNameInputKeyup'

    onRender: ->
      @self = @
      @viewDocument = $.extend(true, {}, @model.attributes.model)

    getViewDocument: ->
      return @viewDocument

    handleModelPropertyInputKeyup: (e) ->
      console.log("handleModelPropertyInputKeyup")
      console.log(e)

    handleSchemaSlotNameInputKeyup: (e) ->
      console.log("handleSchemaSlotNameInputKeyup")
      console.log(e)
