require 'vertx/util/utils.rb'
# Generated from io.vertx.blueprint.microservice.cache.CounterService
module VertxBlueprintCacheCounter
  #  A service interface for global cache and counter management using a cache backend (e.g. Redis).
  #  <p>
  #  This service is an event bus service (aka. service proxy)
  #  </p>
  class CounterService
    # @private
    # @param j_del [::VertxBlueprintCacheCounter::CounterService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end

    # @private
    # @return [::VertxBlueprintCacheCounter::CounterService] the underlying java delegate
    def j_del
      @j_del
    end

    @@j_api_type = Object.new

    def @@j_api_type.accept?(obj)
      obj.class == CounterService
    end

    def @@j_api_type.wrap(obj)
      CounterService.new(obj)
    end

    def @@j_api_type.unwrap(obj)
      obj.j_del
    end

    def self.j_api_type
      @@j_api_type
    end

    def self.j_class
      Java::IoVertxBlueprintMicroserviceCache::CounterService.java_class
    end

    #  First add the counter, then retrieve.
    # @param [String] key counter key
    # @yield async result handler
    # @return [void]
    def add_then_retrieve(key=nil)
      if key.class == String && block_given?
        return @j_del.java_method(:addThenRetrieve, [Java::java.lang.String.java_class, Java::IoVertxCore::Handler.java_class]).call(key, (Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling add_then_retrieve(#{key})"
    end

    #  First add the counter by a <code>increment</code>, then retrieve.
    # @param [String] key counter key
    # @param [Fixnum] increment increment step
    # @yield async result handler
    # @return [void]
    def add_then_retrieve_by(key=nil, increment=nil)
      if key.class == String && increment.class == Fixnum && block_given?
        return @j_del.java_method(:addThenRetrieveBy, [Java::java.lang.String.java_class, Java::JavaLang::Long.java_class, Java::IoVertxCore::Handler.java_class]).call(key, increment, (Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling add_then_retrieve_by(#{key},#{increment})"
    end

    #  First retrieve the counter, then add.
    # @param [String] key counter key
    # @yield async result handler
    # @return [void]
    def retrieve_then_add(key=nil)
      if key.class == String && block_given?
        return @j_del.java_method(:retrieveThenAdd, [Java::java.lang.String.java_class, Java::IoVertxCore::Handler.java_class]).call(key, (Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_then_add(#{key})"
    end

    #  Reset the value of the counter with a certain <code>key</code>
    # @param [String] key counter key
    # @yield async result handler
    # @return [void]
    def reset(key=nil)
      if key.class == String && block_given?
        return @j_del.java_method(:reset, [Java::java.lang.String.java_class, Java::IoVertxCore::Handler.java_class]).call(key, (Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling reset(#{key})"
    end
  end
end
