# cipher-store-vault
HashiCorp Vault cipher store and fetch

commands :- 

1. install vault ->  brew install vault
2. verify install -> vault
3. install autocomplete -> vault -autocomplete-install (restart terminal after this)
4. start vault server -> vault server --dev --dev-root-token-id="00000000-0000-0000-0000-000000000000"
5. export IP -> export VAULT_ADDR='http://127.0.0.1:8200'
6. export token -> export VAULT_TOKEN="00000000-0000-0000-0000-000000000000"
7. verfiy running status -> vault status


Extras (these are done through code, but can also be done from console):- 

1. put a key manually -> vault kv put secret/cipherStore mek=someMEK dek=someDEK.  (secret/cipherStore is out namespace)
2. get a key manually -> vault kv get secret/cipherStore
