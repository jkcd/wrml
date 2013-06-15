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

@Wrmldoc.module "Components.Form", (Form, App, Backbone, Marionette, $, _) ->
	
	class Form.FormWrapper extends App.Views.Layout
		template: "form/form"
		
		tagName: "form"
		attributes: ->
			"data-type": @getFormDataType()
		
		regions:
			formContentRegion: "#form-content-region"
		
		ui:
			buttonContainer: "ul.inline-list"
		
		triggers:
			"submit"														: "form:submit"
			"click [data-form-button='cancel']"	: "form:cancel"
		
		modelEvents:
			"change:_errors" 	: "changeErrors"
			"sync:start"			:	"syncStart"
			"sync:stop"				:	"syncStop"
		
		initialize: ->
			@setInstancePropertiesFor "config", "buttons"
		
		serializeData: ->
			footer: @config.footer
			buttons: @buttons?.toJSON() ? false
		
		onShow: ->
			_.defer =>
				@focusFirstInput() if @config.focusFirstInput
				@buttonPlacement() if @buttons
		
		buttonPlacement: ->
			@ui.buttonContainer.addClass @buttons.placement
		
		focusFirstInput: ->
			@$(":input:visible:enabled:first").focus()
		
		getFormDataType: ->
			if @model.isNew() then "new" else "edit"
		
		changeErrors: (model, errors, options) ->
			if @config.errors
				if _.isEmpty(errors) then @removeErrors() else @addErrors errors
		
		removeErrors: ->
			@$(".error").removeClass("error").find("small").remove()
		
		addErrors: (errors = {}) ->
			for name, array of errors
				@addError name, array[0]
		
		addError: (name, error) ->
			el = @$("[name='#{name}']")
			sm = $("<small>").text(error)
			el.after(sm).closest(".row").addClass("error")
		
		syncStart: (model) ->
			@addOpacityWrapper() if @config.syncing
		
		syncStop: (model) ->
			@addOpacityWrapper(false) if @config.syncing