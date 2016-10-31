require 'vertx/util/utils.rb'
# Generated from io.vertx.blueprint.microservice.account.AccountService
module VertxBlueprintUserAccount
  #  A service interface managing user accounts.
  #  <p>
  #  This service is an event bus service (aka. service proxy).
  #  </p>
  class AccountService
    # @private
    # @param j_del [::VertxBlueprintUserAccount::AccountService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxBlueprintUserAccount::AccountService] the underlying java delegate
    def j_del
      @j_del
    end
    #  Initialize the persistence.
    # @yield the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not.
    # @return [self]
    def initialize_persistence
      if block_given?
        @j_del.java_method(:initializePersistence, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling initialize_persistence()"
    end
    #  Add a account to the persistence.
    # @param [Hash] account a account entity that we want to add
    # @yield the result handler will be called as soon as the account has been added. The async result indicates whether the operation was successful or not.
    # @return [self]
    def add_account(account=nil)
      if account.class == Hash && block_given?
        @j_del.java_method(:addAccount, [Java::IoVertxBlueprintMicroserviceAccount::Account.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::IoVertxBlueprintMicroserviceAccount::Account.new(::Vertx::Util::Utils.to_json_object(account)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling add_account(account)"
    end
    #  Retrieve the user account with certain `id`.
    # @param [String] id user account id
    # @yield the result handler will be called as soon as the user has been retrieved. The async result indicates whether the operation was successful or not.
    # @return [self]
    def retrieve_account(id=nil)
      if id.class == String && block_given?
        @j_del.java_method(:retrieveAccount, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(id,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_account(id)"
    end
    #  Retrieve the user account with certain `username`.
    # @param [String] username username
    # @yield the result handler will be called as soon as the user has been retrieved. The async result indicates whether the operation was successful or not.
    # @return [self]
    def retrieve_by_username(username=nil)
      if username.class == String && block_given?
        @j_del.java_method(:retrieveByUsername, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(username,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_by_username(username)"
    end
    #  Retrieve all user accounts.
    # @yield the result handler will be called as soon as the users have been retrieved. The async result indicates whether the operation was successful or not.
    # @return [self]
    def retrieve_all_accounts
      if block_given?
        @j_del.java_method(:retrieveAllAccounts, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.toJson.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling retrieve_all_accounts()"
    end
    #  Update user account info.
    # @param [Hash] account a account entity that we want to update
    # @yield the result handler will be called as soon as the account has been added. The async result indicates whether the operation was successful or not.
    # @return [self]
    def update_account(account=nil)
      if account.class == Hash && block_given?
        @j_del.java_method(:updateAccount, [Java::IoVertxBlueprintMicroserviceAccount::Account.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::IoVertxBlueprintMicroserviceAccount::Account.new(::Vertx::Util::Utils.to_json_object(account)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling update_account(account)"
    end
    #  Delete a user account from the persistence
    # @param [String] id user account id
    # @yield the result handler will be called as soon as the user has been removed. The async result indicates whether the operation was successful or not.
    # @return [self]
    def delete_account(id=nil)
      if id.class == String && block_given?
        @j_del.java_method(:deleteAccount, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(id,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling delete_account(id)"
    end
    #  Delete all user accounts from the persistence
    # @yield the result handler will be called as soon as the users have been removed. The async result indicates whether the operation was successful or not.
    # @return [self]
    def delete_all_accounts
      if block_given?
        @j_del.java_method(:deleteAllAccounts, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling delete_all_accounts()"
    end
  end
end
