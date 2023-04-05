<?php
    interface Db {
        public function getStores();
        public function getStore($id);
        public function isStoreNameUnique($name);
        public function isUniqueClassName($storeid, $classname);
        public function getIdentifyProviders();
        public function getInternalUser($mail, $passwordhash, $idpid);
        public function canRead($targettype, $targetid=null);
        public function canCreate($targettype, $targetid=null);
        public function canCreateStore();
        public function createStore($name);
        public function canCreateClass($storeid);
        public function createClass($storeid, $class);
    }