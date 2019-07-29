/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

angular.module('zeppelinWebApp').controller('LoginCtrl', LoginCtrl);

function LoginCtrl($scope, $rootScope, $http, $httpParamSerializer, baseUrlSrv, $location, $timeout) {
  'ngInject';

  $scope.SigningIn = false;
  $scope.loginParams = {};

  let initValues = function() {
    let req = new XMLHttpRequest();
    req.open('GET', document.location, false);
    req.send(null);
    let headers = req.getAllResponseHeaders();

    let arr = headers.trim().split(/[\r\n]+/);
    let headerMap = {};
    arr.forEach(function(line) {
      let parts = line.split(': ');
      let header = parts.shift();
      let value = parts.join(': ');
      headerMap[header] = value;
    });
    console.log('Username: ', headerMap['x-user-id']);

    $scope.loginParams = {
      userName: headerMap['x-user-id'],
      password: headerMap['x-user-id'],
    };
  };

  initValues();
  $scope.login = function() {
    console.log('Starting login... ');
    $scope.SigningIn = true;
    $http({
      method: 'GET',
      url: baseUrlSrv.getRestApiBase() + '/login',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      data: $httpParamSerializer({
        'userName': $scope.loginParams.userName,
        'password': $scope.loginParams.password,
      }),
    }).then(function successCallback(response) {
      $rootScope.ticket = response.data.body;
      angular.element('#loginModal').modal('toggle');
      $rootScope.$broadcast('loginSuccess', true);
      $rootScope.userName = $scope.loginParams.userName;
      $scope.SigningIn = false;

      // redirect to the page from where the user originally was
      if ($location.search() && $location.search()['ref']) {
        $timeout(function() {
          let redirectLocation = $location.search()['ref'];
          $location.$$search = {};
          $location.path(redirectLocation);
        }, 100);
      }
    }, function errorCallback(errorResponse) {
      console.log(errorResponse);
      $scope.loginParams.errorText = 'User has not been granted access to Zeppelin: '
        + $scope.loginParams.userName;
      $scope.SigningIn = false;
    });
  };

  // handle session logout message received from WebSocket
  $rootScope.$on('session_logout', function(event, data) {
    if ($rootScope.userName !== '') {
      $rootScope.userName = '';
      $rootScope.ticket = undefined;

      setTimeout(function() {
        $scope.loginParams = {};
        $scope.loginParams.errorText = data.info;
        angular.element('.nav-login-btn').click();
      }, 1000);
      let locationPath = $location.path();
      $location.path('/').search('ref', locationPath);
    }
  });

  window.onload = function() {
    // alert('Trying login now');
    $scope.login();
  };

  /*
   ** $scope.$on functions below
   */
  $scope.$on('initLoginValues', function() {
    initValues();
  });

  $scope.$on('login', function() {
    $scope.login();
  });

  $rootScope.$on('login', function() {
    $scope.login();
  });
}
