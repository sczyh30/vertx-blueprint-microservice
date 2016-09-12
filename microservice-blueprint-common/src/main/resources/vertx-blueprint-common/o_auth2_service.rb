require 'vertx-service-discovery/service_discovery'
require 'vertx-auth-oauth2/o_auth2_auth'
require 'vertx/util/utils.rb'
# Generated from io.vertx.blueprint.microservice.common.discovery.OAuth2Service
module VertxBlueprintCommon
  #   for OAuth2 provider services.
  class OAuth2Service
    # @private
    # @param j_del [::VertxBlueprintCommon::OAuth2Service] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxBlueprintCommon::OAuth2Service] the underlying java delegate
    def j_del
      @j_del
    end
    # @param [String] name 
    # @param [Hash{String => Object}] config 
    # @param [Hash{String => Object}] metadata 
    # @return [Hash]
    def self.create_record(name=nil,config=nil,metadata=nil)
      if name.class == String && config.class == Hash && metadata.class == Hash && !block_given?
        return Java::IoVertxBlueprintMicroserviceCommonDiscovery::OAuth2Service.java_method(:createRecord, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class]).call(name,::Vertx::Util::Utils.to_json_object(config),::Vertx::Util::Utils.to_json_object(metadata)) != nil ? JSON.parse(Java::IoVertxBlueprintMicroserviceCommonDiscovery::OAuth2Service.java_method(:createRecord, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class]).call(name,::Vertx::Util::Utils.to_json_object(config),::Vertx::Util::Utils.to_json_object(metadata)).toJson.encode) : nil
      end
      raise ArgumentError, "Invalid arguments when calling create_record(name,config,metadata)"
    end
    # @param [::VertxServiceDiscovery::ServiceDiscovery] discovery 
    # @param [Hash{String => Object}] filter 
    # @param [Hash{String => Object}] consumerConfiguration 
    # @yield 
    # @return [void]
    def self.get_o_auth2_provider(discovery=nil,filter=nil,consumerConfiguration=nil)
      if discovery.class.method_defined?(:j_del) && filter.class == Hash && block_given? && consumerConfiguration == nil
        return Java::IoVertxBlueprintMicroserviceCommonDiscovery::OAuth2Service.java_method(:getOAuth2Provider, [Java::IoVertxServicediscovery::ServiceDiscovery.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(discovery.j_del,::Vertx::Util::Utils.to_json_object(filter),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ::Vertx::Util::Utils.safe_create(ar.result,::VertxAuthOauth2::OAuth2Auth) : nil) }))
      elsif discovery.class.method_defined?(:j_del) && filter.class == Hash && consumerConfiguration.class == Hash && block_given?
        return Java::IoVertxBlueprintMicroserviceCommonDiscovery::OAuth2Service.java_method(:getOAuth2Provider, [Java::IoVertxServicediscovery::ServiceDiscovery.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(discovery.j_del,::Vertx::Util::Utils.to_json_object(filter),::Vertx::Util::Utils.to_json_object(consumerConfiguration),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ::Vertx::Util::Utils.safe_create(ar.result,::VertxAuthOauth2::OAuth2Auth) : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling get_o_auth2_provider(discovery,filter,consumerConfiguration)"
    end
  end
end
