package geb.mobile.ios.specification

import geb.mobile.ios.views.UICatalogAppView
import geb.spock.GebSpec

/**
 * Created by gmueksch on 01.09.14.
 */

/**
 * Sample test case for iOS app calculator native app:
 */

class UICatalogAppSpec extends GebSpec {

    def "open test-app and test sum of two numbers"(){
        given: "Land on home page"
        at UICatalogAppView

        when:"Enter two numbers"
        insertTextElement1 = "12"
        insertTextElement2 = "22"

        and: "Click on sum up button"
        sumElement.click()

        then: "Check result"
        resultField == "34"
    }
}
